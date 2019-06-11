package com.demo.vrdemo.client;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class SpringbootCamelRestdslApiApplication {

	@Autowired
	private VRDemoClient vrDemoClient;

	public static void main(String[] args) {
		SpringApplication.run(SpringbootCamelRestdslApiApplication.class, args);
	}

	@Component
	class TestRoute extends RouteBuilder {

		@Override
		public void configure() {
			restConfiguration()
			.component("servlet").port(8088)
			.bindingMode(RestBindingMode.json);

			rest("/locations").produces("application/json")
			.get("/clear").to("direct:clear")
			.get("/add").to("direct:addLocation");

			from("direct:clear").process(new Processor() {
				@Override
				public void process(Exchange exchange) throws Exception {
					vrDemoClient.sendClear();
				}
			}).transform().simple("Locations cleared");;

			from("direct:addLocation").process(new Processor() {
				@Override
				public void process(Exchange exchange) throws Exception {
					String lat = exchange.getIn().getHeader("lat", String.class); 
					String lng = exchange.getIn().getHeader("lng", String.class); 
					String name = exchange.getIn().getHeader("name", String.class); 
					vrDemoClient.sendLocation(lat, lng, name);
				}
			}).transform().simple("Location ${header.name} added");
		}
	}
}
