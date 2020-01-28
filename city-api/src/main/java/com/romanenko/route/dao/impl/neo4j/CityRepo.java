package com.romanenko.route.dao.impl.neo4j;

import com.romanenko.route.dao.impl.neo4j.model.City;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Collection;

public interface CityRepo extends Neo4jRepository<City, Long> {
    /**
     * Please note, that of version 3.5.14 this algorithm will fail if directly connected cities are connected with
     * connections that cost more than indirect connections.
     */
    @Query("Match(n:City {name:{0}}) " +
            "call algo.bfs.stream(\"City\", \"CityConnection\", \"OUTGOING\", id(n), {maxCost:{1}, weightProperty:'time'}) " +
            "yield nodeIds " +
            "unwind nodeIds as nodeId " +
            "return algo.asNode(nodeId)")
    Collection<City> findAllCitiesAround(String startCityName, int maximumMinutes);

    @Query("UNWIND split(\"" +
            "Lutadmad,Vebihzaz,Mijumwe,Obikiona,Wahekuhom,Luromup,Gifkitgok,Girzucavo,Zunlicu,Forerror,Vovtidri," +
            "Fugokdah,Uholowpiv,Asoovazen,Beogivub,Mijuljem,Putsojab,Etlufom,Juhibopil,Amagakcif,Lerrijkud,Wozusu," +
            "Kerimgaz,Omiepnes,Pespiju,Bajivwo,Bemeszif,Duwtizu,Vilretdu,Vaptupru,Icisefula,Najelu,Kazeccic,Nadelenu," +
            "Rurtirdi,Emisobsum,Huwkorfo,Hofzahun,Tadicu,Gadvedab,Tafalica,Ucerastap,Datsija,Nuzunej,Nowilug,Opeorzic," +
            "Vifcocta,Fovejo,Efeogoheh,Iwucettu,Bekfazoc,Tuputwon,Belhamefu,Jadmuzpe,Raupici,Sefubkic,Makburi,Oguzano," +
            "Gehvukfo,Novaal,Lazurzi,Inruiw,Hagegco,Wipdokhej,Tapuha,Edeonuve,Sajejmag,Rujvokri,Janekhes,Larwavun," +
            "Ekihure,Emjetbe,Depinon,Fezwucit,Zosciaf,Fezajna,Linrolfew,Garakuz,Jenjotkor,Giavmus,Dodgugkon,Orpizfuc," +
            "Buzcuoga,Rafofco,Seckuro,Rozkicil,Anbive,Kewgoebi,Obsutim,Pageazi,Fansopek,Safjumun,Ukierem,Ozozevre,Pekonlus," +
            "Dowipwa,Kusijus,Jedwaoju,Vutgapmo,Hiutcap\", \",\")\n" +
            "AS cityName\n" +
            "MERGE (a: City { name : cityName})\n" +
            "WITH a\n" +
            "MATCH (firstCity: City), (secondCity: City)\n" +
            "WITH firstCity, secondCity WHERE rand() < 0.015 AND firstCity <> secondCity\n" +
            "MERGE (firstCity)-[:CityConnection { time: toInt(rand()*350)+10}]->(secondCity)\n" +
            "RETURN secondCity")
    void generateSampleData();
}
