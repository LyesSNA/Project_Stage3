package org.paumard.elevator.student;

import org.paumard.elevator.Elevator;
import org.paumard.elevator.model.Person;

import java.time.LocalTime;
import java.util.List;

public class DumbElevator implements Elevator {
    private static int[] floors = {2, 3};
    private static int round = 0;

    public DumbElevator(int capacity) {
    }

    @Override
    public void startsAtFloor(LocalTime time, int initialFloor) {
    }

    @Override
    public void peopleWaiting(List<List<Person>> peopleByFloor) {

    }

    @Override
    public List<Integer> chooseNextFloors() {
        return List.of(floors[round++]);
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