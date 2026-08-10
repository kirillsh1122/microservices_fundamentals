Feature: Song processing

  Scenario: Upload song resource
    Given the resource-service is running
    And the song-service is running
    And A valid .mp3 file "valid-sample-with-required-tags.mp3"
    When the user uploads the .mp3 file to the resource-service
    Then the resource-service should return a resource ID
    And the resource is present in resource-db
    And the resource is present in song-db
    And the resource is present in blob-storage