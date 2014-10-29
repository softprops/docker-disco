package dockerdisco

import tugboat.{ ContainerDetails, Docker, Event }
import dockerwatch.Watch
import java.util.concurrent.ConcurrentHashMap
import scala.concurrent.{ ExecutionContext, Future }

trait Store {
  def add(svc: Service)
  def remove(svc: Service)
}

case class Service(id: String, container: ContainerDetails)

case class Disco
 (docker: Docker, watch: Watch, store: Store)
 (implicit ec: ExecutionContext) {
  private[this] val cache = new ConcurrentHashMap[String, Service]()

  def apply(): Watch.Stop = {
    docker.containers.list()
      .foreach(_.foreach { container =>
        service(container.id).foreach(store.add)
       })
    watch {
      case Event.Record("start", id, _, _) =>
        service(id).foreach(store.add)
      case Event.Record("die", id, _, _) =>
        service(id).foreach(store.remove)
      case _ =>
    }
  }

  def service(id: String): Future[Service] =
    cache.get(id) match {
      case null =>
        docker.containers.get(id)().map(_.get).map { container =>
          val svc = Service(id, container)
          cache.put(id, svc)
          svc
        }
      case svc =>
        Future.successful(svc)
    }
}
