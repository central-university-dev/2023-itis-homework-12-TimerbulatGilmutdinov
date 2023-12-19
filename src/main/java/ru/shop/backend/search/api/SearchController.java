package ru.shop.backend.search.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shop.backend.search.model.SearchResult;
import ru.shop.backend.search.model.SearchResultElastic;
import ru.shop.backend.search.repository.ItemDbRepository;
import ru.shop.backend.search.service.SearchService;

import java.util.Optional;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController implements SearchApi{
    private final ItemDbRepository itemDbRepository;
    private final SearchService service;
    public SearchResult find(String text, int regionId){
        return service.getSearchResult( regionId,  text);
    }
    public ResponseEntity<SearchResultElastic> finds(String text, int regionId) {
        if (service.isNumeric(text)) {
            Optional<Long> itemId = itemDbRepository.findBySku(text);
            if (itemId.isEmpty()) {
                var catalogue = service.getByName(text);
                if (catalogue.size() > 0) {
                    return ResponseEntity.ok().body(new SearchResultElastic(catalogue));
                }
                return ResponseEntity.ok().body(new SearchResultElastic(service.getAllFull(text)));
            }
            try {
                return ResponseEntity.ok().body(new SearchResultElastic(service.getByItemId(itemId.toString())));
            } catch (Exception e) {
            }
        }
        return ResponseEntity.ok().body(new SearchResultElastic(service.getAllFull(text)));
    }
}
