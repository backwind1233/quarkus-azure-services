package io.quarkiverse.azure.services.together.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@EnabledIfSystemProperty(named = "azure.test", matches = "true")
class StorageBlobResourceTest {

    @Test
    void azureStorageBlob() {

        final String container = "container-quarkus-azure-storage-blob";
        final String blobName = "StorageBlobResourceTest-" + UUID.randomUUID().toString() + ".txt";
        final String body = "Hello quarkus-azure-storage-blob at " + LocalDateTime.now();

        try {
            // Create
            given()
                    .when()
                    .body(body)
                    .post("/quarkus-services-azure-storage-blob/" + container + "/" + blobName)
                    .then()
                    .statusCode(201);

            // Read
            given()
                    .when().get("/quarkus-services-azure-storage-blob/" + container + "/" + blobName)
                    .then()
                    .statusCode(200)
                    .body(is(body));

            // List
            given()
                    .when().get("/quarkus-services-azure-storage-blob/" + container)
                    .then()
                    .statusCode(200)
                    .body(containsString(blobName)); // there may exist blobs created by other runs of this test

            // Update
            final String updatedBody = body + " updated";
            given()
                    .when()
                    .body(updatedBody)
                    .post("/quarkus-services-azure-storage-blob/" + container + "/" + blobName)
                    .then()
                    .statusCode(201);

            given()
                    .when().get("/quarkus-services-azure-storage-blob/" + container + "/" + blobName)
                    .then()
                    .statusCode(200)
                    .body(is(updatedBody));
        } finally {
            given()
                    .when()
                    .delete("/quarkus-services-azure-storage-blob/" + container + "/" + blobName)
                    .then()
                    .statusCode(204);

            given()
                    .when().get("/quarkus-services-azure-storage-blob/" + container + "/" + blobName)
                    .then()
                    .statusCode(404);
        }
    }
}
