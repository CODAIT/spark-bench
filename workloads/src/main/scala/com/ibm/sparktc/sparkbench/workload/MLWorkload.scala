package com.ibm.sparktc.sparkbench.workload

abstract class MLWorkload(conf: WorkloadConfig) extends Workload(conf){

//      println(compact(render(Map("loadTime" -> loadTime, "trainingTime" -> trainingTime, "testTime" -> testTime, "saveTime" -> saveTime))))
  def load()
  def train()
  def test()
  def save()

}
