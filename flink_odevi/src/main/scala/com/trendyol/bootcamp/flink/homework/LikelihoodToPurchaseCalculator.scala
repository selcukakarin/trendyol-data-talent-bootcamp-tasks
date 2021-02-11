package com.trendyol.bootcamp.flink.homework

import com.trendyol.bootcamp.flink.common._

case class PurchaseLikelihood(userId: Int, productId: Int, likelihood: Double)

object LikelihoodToPurchaseCalculator {

  val l2pCoefficients = Map(
    AddToBasket         -> 0.4,
    RemoveFromBasket    -> -0.2,
    AddToFavorites      -> 0.7,
    RemoveFromFavorites -> -0.2,
    DisplayBasket       -> 0.5
  )

  def main(args: Array[String]): Unit = ???

}
