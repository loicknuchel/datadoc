package com.criteo.datadoc.experiment.eventsourcing.es.documentation

import com.criteo.datadoc.experiment.eventsourcing.es.common.Event

sealed trait DocEvent extends Event // all events related to documentation aggregate
