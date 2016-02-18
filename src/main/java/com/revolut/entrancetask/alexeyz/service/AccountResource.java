package com.revolut.entrancetask.alexeyz.service;

import com.revolut.entrancetask.alexeyz.domain.Account;
import com.revolut.entrancetask.alexeyz.dto.MoneyTransfer;
import com.revolut.entrancetask.alexeyz.persistence.PersistenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.math.BigDecimal;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * Account REST controller
 */
@Path("account")
public class AccountResource {
    public static final Logger log = LoggerFactory.getLogger(AccountResource.class);
    /**
     * Transfers the given amount from the fromId account to toId
     *
     * @param tf MoneyTransfer object in JSON form
     *
     * @return Operation result: 200 if ok, 400 if account Ids are invalid, money amount is too much, 500 on internal server error
     */
    @POST
    @Path("/transfer")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response transfer(@Valid MoneyTransfer tf) {
        log.debug("Incoming request to transfer {}", tf);
        final EntityManager em  = PersistenceUtil.getEntityManagerFactory().createEntityManager();

        // Use resource-managed tx
        em.getTransaction().begin();
        try {
            // Do the validation and transfer logic
            validateAndPerformTransfer(tf, em);

            // Flush
            em.getTransaction().commit();
        } catch (WebApplicationException wae) {
            log.error("Unable to transfer money", wae);
            em.getTransaction().rollback();
            throw wae;
        } catch (Throwable th) {
            log.error("Unable to transfer money", th);
            em.getTransaction().rollback();
            throw new WebApplicationException(th.getMessage());
        } finally {
            em.close();
        }

        return Response.ok().build();
    }

    // Performs validation and if passed, performs money transfer between accounts
    private void validateAndPerformTransfer(MoneyTransfer tf, EntityManager em) {
        // Validation
        if (tf.getAmount().compareTo(BigDecimal.ZERO) == 0)
            throw new WebApplicationException("Has no sense to transfer zero money", BAD_REQUEST);

        if (tf.getAmount().compareTo(BigDecimal.ZERO) < 0)
            throw new WebApplicationException("Negative amount transfer is prohibited", BAD_REQUEST);

        if (tf.getFromAccountId() == tf.getToAccountId())
            throw new WebApplicationException("Has no sense to transfer funds to the same account", BAD_REQUEST);

        final Account from = em.find(Account.class, tf.getFromAccountId());
        if (from == null)
            throw new WebApplicationException("Account with id not found: " + tf.getFromAccountId(), BAD_REQUEST);

        if (from.getAmount().compareTo(tf.getAmount()) < 0)
            throw new WebApplicationException("Insufficient funds", BAD_REQUEST);

        final Account to = em.find(Account.class, tf.getToAccountId());
        if (to == null)
            throw new WebApplicationException("Account with id not found: " + tf.getToAccountId(), BAD_REQUEST);

        // Transfer
        from.add(tf.getAmount().negate());
        to.add(tf.getAmount());

        // Save
        em.persist(to);
        em.persist(from);
    }
}
