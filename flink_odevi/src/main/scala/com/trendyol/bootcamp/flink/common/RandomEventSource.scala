package com.trendyol.bootcamp.flink.common

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichParallelSourceFunction, SourceFunction}

import scala.util.Random

class RandomEventSource extends RichParallelSourceFunction[Event] {

  var cancelled: Boolean = false
  var maxTs: Long        = System.currentTimeMillis() / 1000
  var random: Random     = _

  val eventTypes = List(
    AddToFavorites,
    RemoveFromFavorites,
    AddToBasket,
    RemoveFromBasket,
    DisplayBasket,
    CompletePurchase,
    Logout,
    Login,
    DisplayOrders
  )

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    random = new Random()
  }

  override def run(ctx: SourceFunction.SourceContext[Event]): Unit =
    while (!cancelled) {
      val newMaxTs = maxTs + random.nextInt(10000)

      ctx.collect(
        Event(
          1 + random.nextInt(10),
          1 + random.nextInt(10),
          eventTypes(random.nextInt(eventTypes.size)),
          newMaxTs
        )
      )

      maxTs = newMaxTs

      Thread.sleep(random.nextInt(50))
    }

  override def cancel(): Unit =
    cancelled = true

}
