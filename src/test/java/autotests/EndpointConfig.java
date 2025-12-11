package autotests;

import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;

public class EndpointConfig {

    @Bean
    public HttpClient duckService(){
        return new HttpClientBuilder()
                .requestUrl("http://localhost:2222")
                .build();
        }
    }
