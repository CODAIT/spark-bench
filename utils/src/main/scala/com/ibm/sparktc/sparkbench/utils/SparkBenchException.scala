package com.ibm.sparktc.sparkbench.utils

case class SparkBenchException(
                                m: String,
                                ex: Throwable = new Exception
                              ) extends Exception(m,ex)

