package com.example.order;

import com.example.order.service.OrderService;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@SpringBootApplication
public class OrderApplication {

	private static final Logger logger = Logger.getLogger(OrderApplication.class.getName());

	private Server server;

	private void start() throws IOException {

		int port = 50051;
		server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
				.addService(new OrderService())
				.build()
				.start();

		logger.info("Server started, listening on " + port);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.err.println("*** shutting down gRPC server since JVM is shutting down");
			try {
				OrderApplication.this.stop();
			} catch (InterruptedException e) {
				e.printStackTrace(System.err);
			}
			System.err.println("*** server shut down");
		}));
	}

	private void stop() throws InterruptedException {
		if (server != null) {
			server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
		}
	}


	private void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}


	public static void main(String[] args) throws IOException, InterruptedException {

		final OrderApplication server = new OrderApplication();
		server.start();
		server.blockUntilShutdown();

	}
}
