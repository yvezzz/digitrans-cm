package com.camtech.digitranscm.service;

record PendingSync(String key, Object value, Operation operation) {
    enum Operation { SET, DELETE }
}
