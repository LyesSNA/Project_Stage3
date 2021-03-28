package org.paumard.elevator;

import org.paumard.elevator.event.Event;
import org.paumard.elevator.model.Person;
import org.paumard.elevator.model.WaitingList;
import org.paumard.elevator.student.DumbElevator;
import org.paumard.elevator.student.JoseElevator;
import org.paumard.elevator.system.ShadowElevator;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Building {

    public static final int ELEVATOR_CAPACITY = 15;
    public static final int ELEVATOR_LOADING_CAPACITY = 3;
    public static final int MAX_DISPLAYED_FLOORS = 5;
    public static final int MAX_FLOOR = 10;
    public static final LocalTime START_TIME = LocalTime.of(6, 0, 0);
    public static final LocalTime END_TIME = LocalTime.of(8, 30, 0);
    public static final LocalTime END_OF_DAY = LocalTime.of(9, 30, 0);

    public static void main(String[] args) {

        NavigableMap<LocalTime, Event> events = new TreeMap<>();

        LocalTime time = START_TIME;
        Event startEvent = new Event(Event.ELEVATOR_STARTS);
        events.put(time, startEvent);

        WaitingList peopleWaitingPerFloor = new WaitingList();
        Elevator elevator = new DumbElevator(ELEVATOR_CAPACITY);


        int totalNumberOfPeople = peopleWaitingPerFloor.countPeople();
        elevator.peopleWaiting(peopleWaitingPerFloor.getLists());
        ShadowElevator shadowElevator = new ShadowElevator(ELEVATOR_CAPACITY, peopleWaitingPerFloor);

        peopleWaitingPerFloor.print();

        while (!shadowElevator.isStopped() && time.isBefore(END_OF_DAY)) {

            elevator.timeIs(time);

            if (time.equals(END_TIME)) {
                System.out.printf("\n[%s]No more people are coming.\n", time.toString());
                shadowElevator.lastPersonArrived();
                elevator.lastPersonArrived();
            }

            if (!events.containsKey(time)) {
                if (time.isBefore(END_TIME)) {
                    totalNumberOfPeople += addNewPersonToWaitingLists(time, peopleWaitingPerFloor, elevator);
                }
                time = time.plusSeconds(3);
                continue;
            }

            Event nextEvent = events.get(time);
            events.remove(time);

            if (nextEvent.getName().equals(Event.ELEVATOR_STARTS)) {

                Event event = Event.fromElevatorStart(time, elevator, shadowElevator);
                LocalTime arrivalTime = event.getTimeOfArrivalFrom(time);
                events.put(arrivalTime, event);

            } else if (nextEvent.getName().equals(Event.ARRIVES_AT_FLOOR)) {

                Event event = Event.fromArrivesAtFloor(time, elevator, shadowElevator);
                LocalTime arrivalTime = event.getTimeOfArrivalFrom(time);
                events.put(arrivalTime, event);

            } else if (nextEvent.getName().equals(Event.DOOR_OPENING)) {

                Event event = Event.fromDoorOpening(time, elevator, shadowElevator);
                LocalTime arrivalTime = event.getTimeOfArrivalFrom(time);
                events.put(arrivalTime, event);

            } else if (nextEvent.getName().equals(Event.DOOR_CLOSING)) {

                Event event = Event.fromDoorClosing(time, shadowElevator);
                LocalTime arrivalTime = time.plus(event.getDuration());
                events.put(arrivalTime, event);

            } else if (nextEvent.getName().equals(Event.LOADING_FIRST_PERSON)) {

                Event event = Event.fromLoadingFirstPerson(time, shadowElevator, elevator, nextEvent);
                LocalTime arrivalTime = event.getTimeOfArrivalFrom(time);
                events.put(arrivalTime, event);

            } else if (nextEvent.getName().equals(Event.LOADING_NEXT_PERSON)) {

                Event event = Event.fromLoadingNextPerson(time, shadowElevator, elevator, nextEvent);
                LocalTime arrivalTime = event.getTimeOfArrivalFrom(time);
                events.put(arrivalTime, event);

            } else if (nextEvent.getName().equals(Event.UNLOADING_FIRST_PERSON)) {

                Event event = Event.fromUnloadingFirstPerson(time, elevator, shadowElevator, nextEvent);

                LocalTime arrivalTime = event.getTimeOfArrivalFrom(time);
                events.put(arrivalTime, event);

            } else if (nextEvent.getName().equals(Event.UNLOADING_NEXT_PERSON)) {

                Event event = Event.fromUnloadingNextPerson(time, elevator, shadowElevator, nextEvent);

                LocalTime arrivalTime = event.getTimeOfArrivalFrom(time);
                events.put(arrivalTime, event);

            } else if (nextEvent.getName().equals(Event.STAND_BY_AT_FLOOR)) {

                Event event = Event.fromStandByAtFloor(time, elevator, shadowElevator);

                LocalTime arrivalTime = event.getTimeOfArrivalFrom(time);
                events.put(arrivalTime, event);

            } else if (nextEvent.getName().equals(Event.STOPPING_AT_FLOOR)) {

                shadowElevator.stopping();
            }

            if (time.isBefore(END_TIME)) {
                totalNumberOfPeople += addNewPersonToWaitingLists(time, peopleWaitingPerFloor, elevator);
            }
            time = time.plusSeconds(3);
        }
        peopleWaitingPerFloor.print();
        shadowElevator.printPeople();
        System.out.printf("[%s] Times up\n", time);
        System.out.println("People loaded: " + shadowElevator.getCount());
        System.out.println("Max people loaded: " + shadowElevator.getMaxLoad());
        Event.durations.forEach(
                (duration, count) ->
                        System.out.printf("%2dh %2dmn %2ds -> %d\n", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart(), count)
        );

        long numberOfPeople =
                Event.durations.entrySet().stream().mapToLong(Map.Entry::getValue).sum();
        Duration maxDuration =
                Event.durations.entrySet().stream().map(Map.Entry::getKey).max(Comparator.naturalOrder()).orElseThrow();
        LongSummaryStatistics stats = Event.durations.entrySet().stream()
                .collect(Collectors.summarizingLong(entry -> entry.getKey().getSeconds() * entry.getValue()));
        Duration averageDuration = Duration.ofSeconds((long) stats.getAverage());

        System.out.println("Number of people taken = " + numberOfPeople);
        System.out.printf("Average waiting time = %dmn %ds\n",
                averageDuration.toMinutesPart(), averageDuration.toSecondsPart());
        System.out.printf("Max waiting time = %dh %dmn %ds\n",
                maxDuration.toHoursPart(), maxDuration.toMinutesPart(), maxDuration.toSecondsPart());
    }

    private static int addNewPersonToWaitingLists(LocalTime time, WaitingList peopleWaitingPerFloor, Elevator elevator) {
        Optional<Map.Entry<Integer, Person>> newPersonWaiting = peopleWaitingPerFloor.addNewPeopleToLists(time);
        if (newPersonWaiting.isPresent()) {
            int floor = newPersonWaiting.orElseThrow().getKey();
            Person person = newPersonWaiting.orElseThrow().getValue();
            elevator.newPersonWaitingAtFloor(floor, person);
            System.out.printf("\n[%s] %s calls the elevator from floor %d to go to floor %d\n", time, person.getName(), floor, person.getDestinationFloor());
            System.out.printf("Waiting list is now:\n");
            peopleWaitingPerFloor.print();
            return 1;
        } else {
            return 0;
        }
    }
}
