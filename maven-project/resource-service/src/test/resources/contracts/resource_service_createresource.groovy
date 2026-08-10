package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "create-resource"

    description "Should create a resource from an audio/mpeg request"

    request {
        method POST()
        url("/resources")

        headers {
            contentType("audio/mpeg")
        }

        body(
                fileAsBytes("valid-sample-with-required-tags.mp3")
        )
    }

    response {
        status OK()

        headers {
            contentType("application/json")
        }

        body(
                id: 123
        )
    }
}