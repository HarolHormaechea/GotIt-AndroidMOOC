package edu.hh.repositories;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.hh.datamodel.Notification;
import edu.hh.datamodel.UserData;

/**
 * Repository to keep track of who follows who in this mess
 * of program!
 * 
 * @author Harold
 *
 */
@Repository
public interface PendingNotificationsRepository extends CrudRepository<Notification, Long>{
	public Collection<Notification> findByNotificationTarget(UserData notificationTarget);
}
