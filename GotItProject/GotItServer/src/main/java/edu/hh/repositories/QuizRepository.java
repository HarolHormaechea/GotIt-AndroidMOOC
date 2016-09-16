package edu.hh.repositories;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.hh.datamodel.Quiz;
import edu.hh.datamodel.UserData;

/**
 * Repository for teen objects!
 * 
 * 
 * @author Harold
 *
 */
@Repository
public interface QuizRepository extends CrudRepository<Quiz, Long> {
	public Collection<Quiz> findByAuthor(UserData author);
	public Quiz findFirstById(int id);
}