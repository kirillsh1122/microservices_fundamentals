package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    label("resourceMessage")
    input {
        triggeredBy("triggerServing()")
    }
    outputMessage {
        sentTo("resource-topic")
        body(1111111111)
    }
}