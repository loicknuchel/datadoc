package com.criteo.datadoc.experiment.eventsourcing.es.impl1.documentation

import com.criteo.datadoc.experiment.eventsourcing.es.common.Command

sealed trait DocCommand extends Command // all commands related to documentation
case class UpdateDoc() extends DocCommand
