package com.example.demo;


import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class DojoStreamTest {

    @Test
    void converterData(){
        List<Player> list = CsvUtilFile.getPlayers();
        assert list.size() == 18207;
    }

    @Test
    void jugadoresMayoresA35SegunClub(){
        List<Player> list = CsvUtilFile.getPlayers();
         list.parallelStream().filter(player -> player.age >= 35)
                .flatMap(player1 -> list.parallelStream()
                        .filter(player2 -> player1.club.equals(player2.club))
                ).distinct()
                .collect(Collectors.groupingBy(Player::getClub));
    }

    @Test
    void mejorJugadorConNacionalidadFrancia(){
        //France
        List<Player> players = CsvUtilFile.getPlayers().stream().filter(jugador -> jugador.national.equals("France")).collect(Collectors.toList());
        List<Player> playerSort = players.stream().sorted(Comparator.comparing(jugador -> jugador.winners)).collect(Collectors.toList());
        Collections.reverse(playerSort);
        Player player = playerSort.stream().findFirst().orElse(null);

    }

    @Test
    void clubsAgrupadosPorNacionalidad(){
        List<Player> players = CsvUtilFile.getPlayers();
        Map<String, Set<String>> clubByNationality = players.stream()
                .collect(Collectors.groupingBy(Player::getNational,
                        Collectors.mapping(Player::getClub, Collectors.toSet())));
    }

    @Test
    void clubConElMejorJugador(){
        List<Player> players = CsvUtilFile.getPlayers().stream().sorted(Comparator.comparing(jugador -> jugador.winners)).collect(Collectors.toList());
        Collections.reverse(players);
        Player player = players.stream().findFirst().orElse(null);
        String club = player.getClub();
    }


    @Test
    void mejorJugadorSegunNacionalidad(){
        List<Player> players = CsvUtilFile.getPlayers();
        //obtener mapa con objeto player
        Map<String, Optional<Player>> mejorJugadorSegunNacionalidad = players.stream()
                .collect(Collectors.groupingBy(Player::getNational,
                        Collectors.maxBy(Comparator.comparing(Player::getWinners))));
        //obtener mapa con nombre jugador
        Map<String, String> mejorJugadorSegunNacionalidadNombre = mejorJugadorSegunNacionalidad.entrySet().stream()
                .filter(entry -> entry.getValue().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get().getName()));
    }


}
