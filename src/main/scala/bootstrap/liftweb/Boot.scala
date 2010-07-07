package bootstrap.liftweb

import net.liftweb.http.LiftRules
import com.arktekk.jmxrestaccess.{ViewResourceImpl, DomainResourceImpl, RootResource}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

class Boot {
  def boot {
    LiftRules.dispatch.append(RootResource).append(DomainResourceImpl).append(ViewResourceImpl)
  }
}

