package com.avaje.tests.query;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.bean.ObjectGraphNode;
import com.avaje.ebean.bean.ObjectGraphOrigin;
import com.avaje.ebeaninternal.api.SpiQuery;
import com.avaje.tests.model.basic.Address;
import com.avaje.tests.model.basic.Customer;
import com.avaje.tests.model.basic.Order;
import com.avaje.tests.model.basic.ResetBasicData;
import org.junit.Test;

import java.util.List;

public class TestAutofetchTuneWithJoin extends BaseTestCase {

  @Test
  public void test() {
    runQuery();
    collectUsage();
  }

  private void runQuery() {

    ResetBasicData.reset();

    Query<Order> q = Ebean.find(Order.class)
        .setAutofetch(true)
        //.fetch("customer")
        //.fetch("customer.contacts")
        .where().lt("id", 3).query();

    List<Order> list = q.findList();

    for (int i = 0; i < list.size(); i++) {
      Order order = list.get(i);
      order.getOrderDate();
      order.getShipDate();
      // order.setShipDate(new Date(System.currentTimeMillis()));
      Customer customer = order.getCustomer();
      customer.getName();
      Address shippingAddress = customer.getShippingAddress();
      if (shippingAddress != null) {
        shippingAddress.getLine1();
        shippingAddress.getCity();
      }
      // customer.getContacts()
    }

    SpiQuery<?> sq = (SpiQuery<?>) q;
    ObjectGraphNode parentNode = sq.getParentNode();
    ObjectGraphOrigin origin = parentNode.getOriginQueryPoint();

    System.out.println("Origin:" + origin.getKey());

    // MetaAutoFetchStatistic metaAutoFetchStatistic =
    // ((DefaultOrmQuery<?>)q).getMetaAutoFetchStatistic();
    // if (metaAutoFetchStatistic != null) {
    // List<NodeUsageStats> nodeUsageStats =
    // metaAutoFetchStatistic.getNodeUsageStats();
    // System.out.println(nodeUsageStats);
    // List<QueryStats> queryStats = metaAutoFetchStatistic.getQueryStats();
    // System.out.println(queryStats);
    // }

    if (q.isAutofetchTuned()) {
      System.out.println("TUNED...");
    }
  }

  private static void collectUsage() {

    Ebean.getDefaultServer().getAutoTune().collectProfiling();
  }

}
