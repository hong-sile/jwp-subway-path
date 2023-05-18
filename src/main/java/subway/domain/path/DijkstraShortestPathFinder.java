package subway.domain.path;

import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.List;
import java.util.Set;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;
import subway.domain.Station;
import subway.domain.section.Section;

@Component
public class DijkstraShortestPathFinder implements ShortestPathFinder {

    @Override
    public Path findShortestPath(final List<Section> sections, final Station startStation,
                                 final Station endStation) {
        final WeightedMultigraph<Station, Section> graph
                = new WeightedMultigraph<>(Section.class);
        initGraph(sections, graph);
        final DijkstraShortestPath<Station, Section> shortestPath
                = new DijkstraShortestPath<>(graph);

        final GraphPath<Station, Section> graphPath = shortestPath.getPath(startStation, endStation);
        final List<Section> sectionList = graphPath.getEdgeList();
        return new Path(graphPath.getVertexList(), sectionList);
    }

    private void initGraph(final List<Section> sections, final WeightedMultigraph<Station, Section> graph) {
        for (final Station station : findAllStation(sections)) {
            graph.addVertex(station);
        }
        for (final Section section : sections) {
            graph.addEdge(section.getPrevStation(), section.getNextStation(), section);
            graph.setEdgeWeight(section, section.getDistance().getValue());
        }
    }

    private Set<Station> findAllStation(final List<Section> sections) {
        return sections.stream()
                .map(section -> List.of(section.getNextStation(), section.getPrevStation()))
                .flatMap(List::stream)
                .collect(toUnmodifiableSet());
    }
}
