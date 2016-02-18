package com.revolut.entrancetask.alexeyz;

import com.revolut.entrancetask.alexeyz.domain.Account;
import com.revolut.entrancetask.alexeyz.domain.User;
import com.revolut.entrancetask.alexeyz.dto.MoneyTransfer;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.rmi.server.UID;

import static com.revolut.entrancetask.alexeyz.persistence.PersistenceUtil.getEntityManagerFactory;
import static java.math.BigDecimal.ZERO;
import static org.junit.Assert.assertEquals;

/**
 * Account REST controller test
 */
public class AccountResourceTest {

    private static HttpServer server;
    private static WebTarget target;

    private static Account from;
    private static Account to;

    public static final BigDecimal INI_FROM_AMOUNT = new BigDecimal(100);
    public static final BigDecimal INI_TO_AMOUNT = ZERO;

    @BeforeClass
    public static void beforeAll() throws Exception {
        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        target = c.target(Main.BASE_URI);

        final EntityManager em = getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        from = new Account();
        User user1 = new User("User Userovich", "User1", new UID().toString());
        em.persist(user1);
        from.setUser(user1);
        em.persist(from);

        to = new Account();
        User user2 = new User("Admin Adminovich", "Admin", new UID().toString());
        em.persist(user2);
        to.setUser(user2);
        em.persist(to);

        em.getTransaction().commit();
        em.close();
    }

    @AfterClass
    public static void afterAll() throws Exception {
        server.shutdownNow();
    }

    @Before
    public void beforeTest() {
        final EntityManager em = getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        from = em.find(Account.class, from.getId());
        from.setAmount(INI_FROM_AMOUNT);
        em.persist(from);
        to = em.find(Account.class, to.getId());
        to.setAmount(INI_TO_AMOUNT);
        em.persist(to);

        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testTransferFromNonExistingAccountFails() {
        Response response = post(new MoneyTransfer(0, from.getId(), 100));
        assertEquals(400, response.getStatus());
        verifyAmountsNotChanged();
    }

    @Test
    public void testTransferToNonExistingAccountFails() {
        Response response = post(new MoneyTransfer(from.getId(), 0, 100));
        assertEquals(400, response.getStatus());
        verifyAmountsNotChanged();
    }

    @Test
    public void testTransferToSameAccountFails() {
        Response response = post(new MoneyTransfer(from.getId(), from.getId(), 100));
        assertEquals(400, response.getStatus());
        verifyAmountsNotChanged();
    }

    @Test
    public void testTransferTooMuchMoneyFails() {
        Response response = post(new MoneyTransfer(from.getId(), to.getId(), from.getAmount().add(BigDecimal.ONE)));
        assertEquals(400, response.getStatus());
        verifyAmountsNotChanged();
    }


    @Test
    public void testTransferZeroAmountFails() {
        Response response = post(new MoneyTransfer(from.getId(), to.getId(), 0));
        assertEquals(400, response.getStatus());
        verifyAmountsNotChanged();
    }

    @Test
    public void testTransferNegativeAmountFails() {
        Response response = post(new MoneyTransfer(from.getId(), to.getId(), -1));
        assertEquals(400, response.getStatus());
        verifyAmountsNotChanged();
    }

    @Test
    public void testTransferOk() {
        Response response = post(new MoneyTransfer(from.getId(), to.getId(), BigDecimal.ONE));
        assertEquals(200, response.getStatus());


        final EntityManager em = getEntityManagerFactory().createEntityManager();

        final BigDecimal fromAmount = em.find(Account.class, from.getId()).getAmount();
        assertEquals(0, INI_FROM_AMOUNT.subtract(BigDecimal.ONE).compareTo(fromAmount));

        final BigDecimal toAmount = em.find(Account.class, to.getId()).getAmount();
        assertEquals(0, INI_TO_AMOUNT.add(BigDecimal.ONE).compareTo(toAmount));

        em.close();
    }

    @Test
    public void testTransferFully() {
        Response response = post(new MoneyTransfer(from.getId(), to.getId(), from.getAmount()));
        assertEquals(200, response.getStatus());

        final EntityManager em = getEntityManagerFactory().createEntityManager();

        final BigDecimal fromAmount = em.find(Account.class, from.getId()).getAmount();
        assertEquals(0, ZERO.compareTo(fromAmount));

        final BigDecimal toAmount = em.find(Account.class, to.getId()).getAmount();
        assertEquals(0, INI_FROM_AMOUNT.compareTo(toAmount));

        em.close();
    }


    private Response post(MoneyTransfer mt) {
        return target.path("account/transfer").request().post(from(mt));
    }

    private static Entity from(MoneyTransfer mt) {
        return Entity.entity(mt, MediaType.valueOf(MediaType.APPLICATION_JSON));
    }

    private void verifyAmountsNotChanged() {
        final EntityManager em = getEntityManagerFactory().createEntityManager();

        final BigDecimal fromAmount = em.find(Account.class, from.getId()).getAmount();
        assertEquals(0, INI_FROM_AMOUNT.compareTo(fromAmount));

        final BigDecimal toAmount = em.find(Account.class, to.getId()).getAmount();
        assertEquals(0, INI_TO_AMOUNT.compareTo(toAmount));

        em.close();
    }
}
