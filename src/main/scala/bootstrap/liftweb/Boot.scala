package bootstrap.liftweb

import net.liftweb.http.LiftRules
import com.arktekk.jmxrestaccess._

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

class Boot {
  def boot {
    LiftRules.dispatch
            .append(RootResource)
            .append(DomainResourceImpl)
            .append(ViewResourceImpl)
            .append(MBeanResource)
            .append(AttributeResource)
  }
}

