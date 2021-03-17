package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.DoseNotExistedStationException;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PathFinder {

    public PathFinder () {}

    public StationGraphPath getShortedPath(
        List<Section> sections,
        Station source,
        Station target
    ) {
        List<Station> stations = sections.stream()
            .flatMap(section -> Stream.of(
                section.getUpStation(),
                section.getDownStation()
            ))
            .distinct()
            .collect(Collectors.toList());

        validateContains(stations, source, target);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        stations.forEach(graph::addVertex);

        for (Section section : sections) {
            graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance()
            );
        }

        return StationGraphPath.of(new DijkstraShortestPath<>(graph).getPath(source, target));
    }

    private void validateContains(List<Station> stations, Station source, Station target) {
        if (!stations.containsAll(Arrays.asList(source, target))) {
            throw new DoseNotExistedStationException();
        }
    }
}