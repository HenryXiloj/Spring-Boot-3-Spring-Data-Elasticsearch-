# Spring Boot 3 + Spring Data Elasticsearch

A modern Spring Boot 3 application demonstrating integration with Elasticsearch using Spring Data Elasticsearch for powerful search capabilities.

📘 Blog Post: [Spring Boot 3 Spring Data  Elasticsearch](https://jarmx.blogspot.com/2023/05/spring-boot-3-spring-data-elasticsearch.html)

## Features

- **Spring Boot 3** - Latest Spring Boot framework with enhanced performance and Java 17+ support
- **Spring Data Elasticsearch** - Simplified Elasticsearch operations with repository pattern
- **RESTful APIs** - Full CRUD operations for document management
- **Advanced Search** - Complex queries, filtering, and aggregations
- **Auto-configuration** - Minimal configuration with Spring Boot auto-configuration
- **Docker Support** - Easy deployment with Docker and Docker Compose

## Prerequisites

- Java 17 or higher
- Maven 3.6+ or Gradle 7+
- Elasticsearch 8.x (compatible with Spring Data Elasticsearch 5.x)
- Docker (optional, for running Elasticsearch)

## Technology Stack

- **Spring Boot 3.x**
- **Spring Data Elasticsearch 5.x**
- **Elasticsearch 8.x**
- **Maven/Gradle**
- **Docker & Docker Compose**

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd spring-boot-elasticsearch-demo
```

### 2. Start Elasticsearch with Docker

```bash
docker-compose up -d elasticsearch
```

### 3. Build and Run the Application

#### Using Maven
```bash
mvn clean install
mvn spring-boot:run
```

#### Using Gradle
```bash
./gradlew build
./gradlew bootRun
```

### 4. Verify Installation

Open your browser and navigate to:
- Application: `http://localhost:8080`
- Elasticsearch: `http://localhost:9200`

## Configuration

### Application Properties

```yaml
spring:
  elasticsearch:
    uris: http://localhost:9200
    username: elastic
    password: changeme
  
  application:
    name: spring-boot-elasticsearch-demo

server:
  port: 8080

logging:
  level:
    org.springframework.data.elasticsearch: DEBUG
```

### Docker Compose

```yaml
version: '3.8'
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ports:
      - "9200:9200"
      - "9300:9300"
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data

volumes:
  elasticsearch_data:
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/elasticsearch/
│   │       ├── ElasticsearchDemoApplication.java
│   │       ├── config/
│   │       │   └── ElasticsearchConfig.java
│   │       ├── controller/
│   │       │   └── ProductController.java
│   │       ├── model/
│   │       │   └── Product.java
│   │       ├── repository/
│   │       │   └── ProductRepository.java
│   │       └── service/
│   │           ├── ProductService.java
│   │           └── ProductServiceImpl.java
│   └── resources/
│       ├── application.yml
│       └── static/
└── test/
    └── java/
        └── com/example/elasticsearch/
            └── ElasticsearchDemoApplicationTests.java
```

## Key Components

### 1. Document Entity

```java
@Document(indexName = "products")
public class Product {
    @Id
    private String id;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;
    
    @Field(type = FieldType.Text)
    private String description;
    
    @Field(type = FieldType.Double)
    private Double price;
    
    @Field(type = FieldType.Keyword)
    private String category;
    
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime createdDate;
    
    // constructors, getters, setters
}
```

### 2. Repository Interface

```java
@Repository
public interface ProductRepository extends ElasticsearchRepository<Product, String> {
    
    List<Product> findByName(String name);
    
    List<Product> findByCategory(String category);
    
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    
    @Query("{\"match\": {\"description\": \"?0\"}}")
    List<Product> findByDescriptionCustomQuery(String description);
    
    Page<Product> findByNameContaining(String name, Pageable pageable);
}
```

### 3. REST Controller

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.save(product));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(productService.searchByName(query));
    }
}
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | Get all products (paginated) |
| GET | `/api/products/{id}` | Get product by ID |
| POST | `/api/products` | Create new product |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |
| GET | `/api/products/search?query={query}` | Search products by name |
| GET | `/api/products/category/{category}` | Get products by category |

## Usage Examples

### Creating a Product

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop for developers",
    "price": 1299.99,
    "category": "Electronics"
  }'
```

### Searching Products

```bash
curl "http://localhost:8080/api/products/search?query=laptop"
```

### Advanced Query Examples

```java
// In your service class
public List<Product> complexSearch(String name, String category, Double minPrice, Double maxPrice) {
    BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
    
    if (name != null) {
        queryBuilder.must(QueryBuilders.matchQuery("name", name));
    }
    
    if (category != null) {
        queryBuilder.filter(QueryBuilders.termQuery("category", category));
    }
    
    if (minPrice != null || maxPrice != null) {
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price");
        if (minPrice != null) rangeQuery.gte(minPrice);
        if (maxPrice != null) rangeQuery.lte(maxPrice);
        queryBuilder.filter(rangeQuery);
    }
    
    NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .build();
    
    SearchHits<Product> searchHits = elasticsearchRestTemplate.search(searchQuery, Product.class);
    return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
}
```

## Testing

### Unit Tests

```bash
mvn test
```

### Integration Tests

```bash
mvn verify
```

### Test with Testcontainers

```java
@SpringBootTest
@Testcontainers
class ProductRepositoryTest {
    
    @Container
    static ElasticsearchContainer elasticsearch = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.11.0")
            .withExposedPorts(9200);
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", elasticsearch::getHttpHostAddress);
    }
    
    @Test
    void shouldSaveAndFindProduct() {
        // Test implementation
    }
}
```

## Performance Considerations

- **Indexing Strategy**: Use bulk operations for large datasets
- **Query Optimization**: Utilize filters over queries when possible
- **Connection Pooling**: Configure appropriate connection pool settings
- **Caching**: Implement caching for frequently accessed data

## Monitoring and Health Checks

Spring Boot Actuator endpoints:
- `/actuator/health` - Application health status
- `/actuator/elasticsearch` - Elasticsearch health check

## Troubleshooting

### Common Issues

1. **Connection refused**: Ensure Elasticsearch is running on the specified port
2. **Index not found**: Check if the index was created properly
3. **Mapping conflicts**: Verify field types match between Java model and Elasticsearch mapping

### Debug Logging

```yaml
logging:
  level:
    org.springframework.data.elasticsearch: DEBUG
    org.elasticsearch.client: DEBUG
```

## Resources

- [Spring Data Elasticsearch Reference](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/)
- [Elasticsearch Documentation](https://www.elastic.co/guide/index.html)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)

## Support

For questions and support, please open an issue in the GitHub repository or contact the development team.
