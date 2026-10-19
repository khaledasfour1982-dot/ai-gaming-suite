import json
import os
import aio_pika

RABBITMQ_HOST = os.getenv("RABBITMQ_HOST", "app_rabbitmq")
RABBITMQ_PORT = int(os.getenv("RABBITMQ_PORT", 5672))
RABBITMQ_USER = os.getenv("RABBITMQ_USER", "user")
RABBITMQ_PASS = os.getenv("RABBITMQ_PASS", "password")

EXCHANGE_NAME = "gameExchange"
EXCHANGE_TYPE = aio_pika.ExchangeType.TOPIC


class RabbitMQPublisher:
    _connection = None
    _channel = None
    _exchange = None

    @classmethod
    async def connect(cls):
        if cls._connection and cls._channel and cls._exchange:
            return cls._exchange

        url = f"amqp://{RABBITMQ_USER}:{RABBITMQ_PASS}@{RABBITMQ_HOST}:{RABBITMQ_PORT}/"

        cls._connection = await aio_pika.connect_robust(url)
        cls._channel = await cls._connection.channel()

        cls._exchange = await cls._channel.declare_exchange(
            EXCHANGE_NAME,
            EXCHANGE_TYPE,
            durable=True
        )

        print("✓ Connected to RabbitMQ (async) and exchange declared")
        return cls._exchange

    @classmethod
    async def publish(cls, routing_key: str, event: dict):
        exchange = await cls.connect()

        body = json.dumps(event).encode("utf-8")

        message = aio_pika.Message(
            body=body,
            content_type="application/json",
            delivery_mode=aio_pika.DeliveryMode.PERSISTENT,
        )

        await exchange.publish(message, routing_key=routing_key)

        print(f"📤 Published event → {routing_key}: {event}")
