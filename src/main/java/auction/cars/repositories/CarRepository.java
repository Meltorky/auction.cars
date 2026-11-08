package auction.cars.repositories;

import auction.cars.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CarRepository extends JpaRepository<Car, UUID> {

    List<Car> findByOwner_Id(UUID ownerId);
}