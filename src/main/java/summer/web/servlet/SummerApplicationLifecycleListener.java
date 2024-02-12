package summer.web.servlet;

import lombok.AllArgsConstructor;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import summer.core.context.SummerApplicationContext;

@AllArgsConstructor
public class SummerApplicationLifecycleListener implements LifecycleListener {
  private static final String BEFORE_DESTROY_EVENT = "before_destroy";

  private final SummerApplicationContext summerApplicationContext;
  @Override
  public void lifecycleEvent(LifecycleEvent event) {
    if (event.getType().equalsIgnoreCase(BEFORE_DESTROY_EVENT)) {
      summerApplicationContext.close();
    }
  }
}
