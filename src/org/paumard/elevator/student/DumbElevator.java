package org.paumard.elevator.student;

import org.paumard.elevator.Building;
import org.paumard.elevator.Elevator;
import org.paumard.elevator.event.DIRECTION;
import org.paumard.elevator.model.Person;

import java.time.LocalTime;
import java.util.List;

public class DumbElevator implements Elevator {
    private DIRECTION direction = DIRECTION.UP;
    private int currentFloor = 1;
    private final String id;


    public DumbElevator(int capacity, String id) {
        this.id = id;
    }
    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void startsAtFloor(LocalTime time, int initialFloor) {
        this.currentFloor = initialFloor;
    }

    @Override
    public void peopleWaiting(List<List<Person>> peopleByFloor) {

    }

    @Override
    public List<Integer> chooseNextFloors() {
        if (direction == DIRECTION.UP) {
            if (currentFloor < Building.MAX_FLOOR) {
                currentFloor++;
            } else {
                this.direction = DIRECTION.DOWN;
                currentFloor--;
            }
        } else {
            if (currentFloor > 1) {
                currentFloor--;
            } else {
                this.direction = DIRECTION.UP;
                currentFloor++;
            }
        }
        return List.of(currentFloor);
    }

    @Override
    public void arriveAtFloor(int floor) {
    }

    @Override
    public void loadPeople(List<Person> person) {
    }

    @Override
    public void unload(List<Person> person) {
    }

    @Override
    public void newPersonWaitingAtFloor(int floor, Person person) {
    }

    @Override
    public void lastPersonArrived() {
    }

    @Override
    public void timeIs(LocalTime time) {
    }

    @Override
    public void standByAtFloor(int currentFloor) {
    }
}