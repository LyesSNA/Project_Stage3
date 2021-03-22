package org.paumard.elevator.student;

import org.paumard.elevator.Elevator;
import org.paumard.elevator.model.Person;

import java.time.LocalTime;
import java.util.List;

public class DumbElevator implements Elevator {

    private int elevatorCapacity;

    public DumbElevator(int elevatorCapacity) {
        this.elevatorCapacity = elevatorCapacity;
    }

    @Override
    public void startsAtFloor(LocalTime time, int initialFloor) {
    }

    @Override
    public void peopleWaiting(List<List<Person>> peopleByFloor) {
    }

    @Override
    public int chooseNextFloor() {
        return 1;
    }

    @Override
    public void arriveAtFloor(int floor) {
    }

    @Override
    public void loadPerson(Person person) {
    }

    @Override
    public void unloadPerson(Person person) {
    }

    @Override
    public void newPersonWaitingAtFloor(int floor, Person person) {
    }

    @Override
    public void lastPersonArrived() {
    }
}
