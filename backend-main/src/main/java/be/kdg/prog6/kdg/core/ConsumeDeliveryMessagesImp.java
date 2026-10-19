//package be.kdg.prog6.kdg.core;
//
//
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Service;
//
//@Service
//public class ConsumeDeliveryMessagesImp {
//
//    @RabbitListener(queues = "order.pickedup.queue")
//    public void handlePickedUp(DeliveryPickedUpEvent event) {
//        // Update order to PICKED UP
//    }
//
//    @RabbitListener(queues = "order.delivered.queue")
//    public void handleDelivered(DeliveryDeliveredEvent event) {
//        // Update order to DELIVERED
//    }
//
//    @RabbitListener(queues = "courier.location.queue")
//    public void handleCourierLocation(CourierLocationEvent event) {
//        // Update courier location for order
//    }
//
//}
