@component
Feature: Resource processing

  Scenario: mp3 resource processing
    Given retrieved resource ID "111" message from resource-service via the kafka topic
    When resource-processor processes the resource ID
    Then get the resource from the resource-service
    And processed the resource metadata
    And sent the processed resource metadata to the song-service