package com.henry.resource;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.henry.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SearchQueryBuilder {

    @Autowired
    private ElasticsearchClient client;

    public List<User> getByName(String text) throws IOException {
        var list = new ArrayList<User>();
        SearchResponse<User> search = client.search(s -> s
                        .index("users")
                        .query(q -> q
                                .term(t -> t
                                        .field("name")
                                        .value(v -> v.stringValue(text))
                                )),
                User.class);

        for (Hit<User> hit: search.hits().hits()) {
            list.add(hit.source());
        }

        return list;
    }

}
