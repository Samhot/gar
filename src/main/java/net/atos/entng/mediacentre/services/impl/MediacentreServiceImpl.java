package net.atos.entng.mediacentre.services.impl;


import fr.wseduc.webutils.Either;
import net.atos.entng.mediacentre.services.MediacentreService;
import org.entcore.common.neo4j.Neo4j;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import static org.entcore.common.neo4j.Neo4jResult.validResultHandler;

public class MediacentreServiceImpl implements MediacentreService {

    private Neo4j neo4j = Neo4j.getInstance();

    @Override
    public void getUserExportData(Handler<Either<String, JsonArray>> handler) {
 /*       String query = "MATCH (p:Profile)<-[HAS_PROFILE]-(pg:ProfileGroup)<-[IN]-(u:User)-[ADMINISTRATIVE_ATTACHMENT]->(s:Structure) " +
                "where p.name = 'Student' " +
                "return u.id, u.lastName, u.displayName, u.firstName, u.structures, u.birthDate, s.UAI limit 25";*/
        String query = "MATCH (p:Profile)<-[HAS_PROFILE]-(pg:ProfileGroup)<-[IN]-(u:User)-[ADMINISTRATIVE_ATTACHMENT]->(s:Structure) " +
                "where p.name = 'Student' " +
                "OPTIONAL MATCH (pg:ProfileGroup)-[DEPENDS]->(s2:Structure) " +
                "where s.UAI <> s2.UAI " +
                "return distinct u.id, u.lastName, u.displayName, u.firstName, u.structures, u.birthDate, s.UAI, s2.UAI order by u.id ";
        neo4j.execute(query, new JsonObject(), validResultHandler(handler));
    }

    @Override
    public void getPersonMef(Handler<Either<String, JsonArray>> handler) {
        String query = "MATCH  (p:Profile)<-[HAS_PROFILE]-(pg:ProfileGroup)<-[IN]-(u:User)-[ADMINISTRATIVE_ATTACHMENT]->(s:Structure) " +
                "where p.name = 'Student' " +
                "return u.id, u.module, s.UAI order by u.id";
        neo4j.execute(query, new JsonObject(), validResultHandler(handler));
    }

    @Override
    public void getEleveEnseignement(Handler<Either<String, JsonArray>> handler) {
        String query = "MATCH  (p:Profile)<-[HAS_PROFILE]-(pg:ProfileGroup)<-[IN]-(u:User)-[ADMINISTRATIVE_ATTACHMENT]->(s:Structure) " +
                "where p.name = 'Student' " +
                "return u.id, s.UAI, u.fieldOfStudy";
        neo4j.execute(query, new JsonObject(), validResultHandler(handler));
    }

    @Override
    public void getTeachersExportData(Handler<Either<String, JsonArray>> handler) {
        String query = "MATCH (p:Profile)<-[HAS_PROFILE]-(pg:ProfileGroup)<-[IN]-(u:User)-[ADMINISTRATIVE_ATTACHMENT]->(s:Structure) " +
                "where p.name = 'Teacher' or p.name = 'Personnel' " +
                "OPTIONAL MATCH (pg:ProfileGroup)-[DEPENDS]->(s2:Structure) " +
                "where s.UAI <> s2.UAI " +
                "return distinct u.id, u.lastName, u.displayName, u.firstName, u.structures, u.birthDate, s.UAI, p.name, s2.UAI, u.functions order by u.id";
        neo4j.execute(query, new JsonObject(), validResultHandler(handler));
    }

    @Override
    public void getPersonMefTeacher(Handler<Either<String, JsonArray>> handler) {
        String query = "MATCH  (p:Profile)<-[HAS_PROFILE]-(pg:ProfileGroup)<-[IN]-(u:User)-[ADMINISTRATIVE_ATTACHMENT]->(s:Structure) " +
                "where p.name = 'Teacher' " +
                "return u.id, u.modules, s.UAI order by u.id";
        neo4j.execute(query, new JsonObject(), validResultHandler(handler));
    }

    @Override
    public void getEtablissement(Handler<Either<String, JsonArray>> handler) {
        String query = " MATCH (s:Structure) OPTIONAL MATCH (s2:Structure)<-[HAS_ATTACHMENT]-(s:Structure)  RETURN s.UAI, s.contract, s.name, s.phone, s2.UAI order by s.UAI";
        // MATCH (s:Structure), (s2:Structure)-[HAS_ATTACHMENT]->(s3:Structure) where s.UAI = s2.UAI or s2 is null  return  s.UAI, s.contract, s.name, s.phone, s3.UAI LIMIT 25
        neo4j.execute(query, new JsonObject(), validResultHandler(handler));
    }

    @Override
    public void getEtablissementMef(Handler<Either<String, JsonArray>> handler) {
        String query = "MATCH (n:User)-[ADMINISTRATIVE_ATTACHMENT]->(s:Structure) RETURN distinct n.module, n.moduleName, s.UAI";
        neo4j.execute(query, new JsonObject(), validResultHandler(handler));
    }

    @Override
    public void getEtablissementMatiere(Handler<Either<String, JsonArray>> handler) {
        String query = "match (sub:Subject)-[SUBJECT]->(s:Structure) return sub.label, sub.code, s.UAI order by s.UAI";
        neo4j.execute(query, new JsonObject(), validResultHandler(handler));
    }

    @Override
    public void getGroupsExportData(Handler<Either<String, JsonArray>> handler) {
        String query = "MATCH (fg:FunctionalGroup) optional match (s:Structure)<-[BELONGS]-(c:Class)<-[DEPENDS]-(pg:ProfileGroup)<-[IN]-(u:User)-[COMMUNIQUE]->(fg:FunctionalGroup) " +
                "return distinct s.UAI, s.name, c.name, c.id, c.externalId, fg.id, fg.externalId, fg.name";
        neo4j.execute(query, new JsonObject(), validResultHandler(handler));
    }

    @Override
    public void getDivisionsExportData(Handler<Either<String, JsonArray>> handler) {
        String query = "MATCH (s:Structure)<-[BELONGS]-(c:Class) " +
                "return distinct s.UAI, s.name, c.name, c.id, c.externalId";
        neo4j.execute(query, new JsonObject(), validResultHandler(handler));
    }

    @Override
    public void getPersonGroupe(Handler<Either<String, JsonArray>> handler) {
        String query = "MATCH (u:User)-[COMMUNIQUE]->(fg:FunctionalGroup)-[BELONGS]->(s:Structure) return distinct fg.id, fg.externalId, u.id, s.UAI";
        neo4j.execute(query, new JsonObject(), validResultHandler(handler));
    }

    @Override
    public void getEnsGroupAndClassMatiere(Handler<Either<String, JsonArray>> handler) {
        String query = "match (u:User)-[t:TEACHES]->(sub:Subject)-[SUBJECT]->(s:Structure) " +
                "return u.id, t.groups, t.classes, sub.code, s.UAI";
        neo4j.execute(query, new JsonObject(), validResultHandler(handler));
    }

    @Override
    public void getInChargeOfExportData(String groupName, Handler<Either<String, JsonArray>> handler) {
        String query = "MATCH (s:Structure)<-[ADMINISTRATIVE_ATTACHMENT]-(u:User)-[IN]->(n:ManualGroup) " +
                "where n.name = {groupName} " +
                "RETURN u.id, u.lastName, u.firstName, u.email, s.UAI";
        JsonObject params = new JsonObject().putString("groupName", groupName);
        neo4j.execute(query, params, validResultHandler(handler));
    }
}
