package com.example.order.service;

import com.example.order.OrderRequest;
import com.example.order.OrderResponse;
import com.example.order.OrderServiceGrpc;
import com.example.order.beans.Order;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;

@GrpcService
public class OrderService extends OrderServiceGrpc.OrderServiceImplBase {

    private final String path = Paths.get(".").toAbsolutePath().normalize()+"/src/main/java/com/example/order/data/order.json";

    @Override
    public void placeOrder(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        try {
            System.out.println("Placing order in order service");
            Order order = new Gson().fromJson(new InputStreamReader(new FileInputStream(path)), Order.class);
            order.products = new ArrayList<>();
            order.products.add(request.getProduct());

            FileWriter writer = new FileWriter(path);
            new GsonBuilder().setPrettyPrinting().create().toJson(order, writer);
            writer.flush();
            writer.close();
            OrderResponse response = OrderResponse.newBuilder().addAllProducts(order.products).setMsg("Order placed").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void updateOrder(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        try {
            Order order = new Gson().fromJson(new InputStreamReader(new FileInputStream(path)), Order.class);
            order.products.add(request.getProduct());

            FileWriter writer = new FileWriter(path);
            new GsonBuilder().setPrettyPrinting().create().toJson(order, writer);
            writer.flush();
            writer.close();
            OrderResponse response = OrderResponse.newBuilder().addAllProducts(order.products).setMsg("Order Updated").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
