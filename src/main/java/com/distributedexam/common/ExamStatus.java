package com.distributedexam.common;

import java.io.Serializable;

public enum ExamStatus implements Serializable {
    SCHEDULED, RUNNING, COMPLETED, CANCELLED
}
