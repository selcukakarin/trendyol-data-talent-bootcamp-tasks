package com.trendyol.bootcamp.flink.common

sealed trait EventType extends Product with Serializable
case object AddToFavorites      extends EventType
case object RemoveFromFavorites extends EventType
case object AddToBasket         extends EventType
case object RemoveFromBasket    extends EventType
case object DisplayBasket       extends EventType
case object CompletePurchase    extends EventType
case object Logout              extends EventType
case object Login               extends EventType
case object DisplayOrders       extends EventType

case class Event(productId: Int, userId: Int, eventType: EventType, timestamp: Long) {

  def toEnhanced: EnhancedEvent =
    EnhancedEvent(s"Product #{$productId}", s"User #{$userId}", eventType, timestamp)
}

case class EnhancedEvent(productName: String, userFullName: String, eventType: EventType, timestamp: Long)
