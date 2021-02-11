package com.trendyol.bootcamp.flink.example

import com.trendyol.bootcamp.flink.common.RandomEventSource
import com.trendyol.bootcamp.flink.common._
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import org.slf4j.{Logger, LoggerFactory}

import java.time.Duration

case class UserStats(userId: Int, eventCount: Int, windowStart: Long, windowEnd: Long)

object EventStreamer {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.enableCheckpointing(60000, CheckpointingMode.AT_LEAST_ONCE)
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(100, 15000))

    val keyedStream = env
      .addSource(new RandomEventSource)
      .filter(e => List(AddToBasket, AddToFavorites, DisplayBasket).contains(e.eventType))
      .assignTimestampsAndWatermarks(
        WatermarkStrategy
          .forBoundedOutOfOrderness(Duration.ofSeconds(10))
          .withTimestampAssigner(
            new SerializableTimestampAssigner[Event] {
              override def extractTimestamp(element: Event, recordTimestamp: Long): Long =
                element.timestamp
            }
          )
      )
      .keyBy(_.userId)

    keyedStream
      .window(TumblingEventTimeWindows.of(Time.seconds(20)))
      .process(
        new ProcessWindowFunction[Event, UserStats, Int, TimeWindow] {
          override def process(
              key: Int,
              context: Context,
              elements: Iterable[Event],
              out: Collector[UserStats]
          ): Unit =
            out.collect(UserStats(key, elements.size, context.window.getStart, context.window.getEnd))
        }
      )
      .addSink(new PrintSinkFunction[UserStats])

    env.execute("Event Streamer")
  }

}
