package com.example.demo;


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import java.util.*;
import java.util.stream.Collectors;

public class DojoReactiveTest {

    @Test
    void converterData(){
        List<Player> list = CsvUtilFile.getPlayers();
        assert list.size() == 18207;
    }



    @Test
    void jugadoresMayoresA35SegunClub(){
        var list = Flux.fromStream(CsvUtilFile.getPlayers().parallelStream()).cache();
        list.filter(player -> player.age >= 35)
                .buffer(10)
                .flatMap(player1 -> list.filter(player2 -> player1.parallelStream().anyMatch(a -> player2.club.equals(a.club))))
                        .distinct()
                        .collectMultimap(Player::getClub);

        list.collectList().block();
    }

    @Test
    void mejorJugadorConNacionalidadFrancia(){
        List<Player> listPlayers = CsvUtilFile.getPlayers();
        var players = Flux.fromIterable(listPlayers).cache();
        players.filter(player -> player.national.equals("France"))
                .sort((j1, j2) -> j1.winners - j2.winners)
                .collectList()
                .flatMapIterable(list -> {
                    Collections.reverse(list);
                    return list;
                })
                .collectList()
                .flatMapMany(Flux::fromIterable).next()
                .subscribe(System.out::println);
    }

    @Test
    void clubsAgrupadosPorNacionalidad(){

        List<Player> listPlayers = CsvUtilFile.getPlayers();
        var players = Flux.fromIterable(listPlayers).cache();

        players
                .groupBy(Player::getNational)
                .flatMap(group -> group.collectList().map(list -> Map.entry(group.key(), list)))
                .map(jugador -> {
                    Set<String> clubes = jugador.getValue().stream().map(Player::getClub).collect(Collectors.toSet());
                    return Map.entry(jugador.getKey(), clubes);
                })
                //.collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .subscribe(entry -> {
                    System.out.println("Nacionalidad: " + entry.getKey());
                    System.out.println("equipos: " + entry.getValue());
                });

    }

    @Test
    void clubConElMejorJugador(){
        List<Player> listPlayers = CsvUtilFile.getPlayers();
        var players = Flux.fromIterable(listPlayers).cache();
        players
                .sort((j1, j2) -> j1.winners - j2.winners)
                .collectList()
                .flatMapIterable(list -> {
                    Collections.reverse(list);
                    return list;
                })
                .collectList()
                .flatMapMany(Flux::fromIterable).next()
                .map(jugador -> jugador.club)
                .subscribe(System.out::println);

    }


    @Test
    void mejorJugadorSegunNacionalidad(){
        List<Player> listPlayers = CsvUtilFile.getPlayers();
        var players = Flux.fromIterable(listPlayers).cache();
        players
                .groupBy(Player::getNational)
                .flatMap(group -> group.reduce((j1, j2) -> j1.winners > j2.winners ? j1 : j2))
                //.collectMap(Player::getNational, player -> player)
                .collectMap(Player::getNational, player -> player.getName())
                .subscribe(System.out::println);
    }



}
