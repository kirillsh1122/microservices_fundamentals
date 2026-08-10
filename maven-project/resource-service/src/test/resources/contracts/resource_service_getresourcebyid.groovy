package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "get-resource-by-id"

    description("should return a resource with the given id")

    request {
        url ("/resources/123")
        method GET()
        headers {
            accept("audio/mpeg")
        }
    }

    response {
        status OK()
        body(
                fileAsBytes("valid-sample-with-required-tags.mp3")
        )
        headers {
            contentType("audio/mpeg")
        }
    }
}
