package org.springframework.samples.petclinic.endpoints;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.samples.petclinic.jaxb2.*;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("jdbc")
@ContextConfiguration(locations = { "classpath:spring/business-config.xml" })

public class ClinicServiceEndPointsTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ClinicService clinicService;

    @Autowired
    private ClinicServiceEndpoints clinicServiceEndpoints;

    /*
    * function to make generating owner easier, can use in different functions when we need to create new GeneratedOwner, or
    * when GeneratedOwner will have new parameters etc in the future, we can easily set them.
    * @return new GeneratedOwner
     */
    public GeneratedOwner generatedOwner() {
        GeneratedOwner owner = new GeneratedOwner();
        owner.setAddress("Lõõtsa 6");
        owner.setCity("Tallinn");
        owner.setTelephone("56574713");
        owner.setLastName("Härsing");
        return owner;
    }

    /*
    * function to make generating pet easier, can use in different functions when we need to create new GeneratedPet, or
    * when GeneratedPet will have new parameters etc in the future, we can easily set them.
    * @return new GeneratedPet
     */

    public GeneratedPet generatedPet() {
        GeneratedPet pet = new GeneratedPet();
        pet.setName("Pitsu");
        pet.setOwnerId(2);
        GeneratedPetType petType = new GeneratedPetType();
        petType.setTypeId(1);
        pet.setType(petType);
        XMLGregorianCalendar calendar = new XMLGregorianCalendarImpl();
        pet.setBirthDate(calendar);
        return pet;
    }


    @Test
    public void addOwner() {
        AddOwnerRequest request = new AddOwnerRequest();
        GeneratedOwner owner = generatedOwner();
        request.setOwnerDetails(owner);
        AddOwnerResponse response = clinicServiceEndpoints.addOwner(request);
        assertNotNull(response);
        getOwnerDetails(response.getId());
    }

    public void getOwnerDetails(int id) {
        GetOwnerRequest request = new GetOwnerRequest();
        request.setId(id);
        GetOwnerResponse response = clinicServiceEndpoints.getOwnerDetails(request);
        assertNotNull(response);
        assertEquals("Härsing", response.getOwner().getLastName());

    }

    @Test
    public void deleteOwner() {
        DeleteOwnerRequest  request = new DeleteOwnerRequest();
        request.setId(10);
        DeleteOwnerResponse response = clinicServiceEndpoints.deleteOwner(request);
        assertNotNull(response);
        assertEquals("if deleting is successful, the response will contain :", -1, response.getId());
    }

    @Test
    public void addPet() throws DatatypeConfigurationException {
       AddPetRequest petRequest = new AddPetRequest();
        GeneratedPet pet = generatedPet();
        petRequest.setPet(pet);
        AddPetResponse response = clinicServiceEndpoints.addPet(petRequest);
        assertNotNull(response);
        getPetDetails(response.getId());
    }

    public void getPetDetails(int id) throws DatatypeConfigurationException {
        GetPetRequest request = new GetPetRequest();
        request.setId(id);
        GetPetResponse response = clinicServiceEndpoints.getPetDetails(request);
        assertNotNull(response);
        assertEquals("Pitsu", response.getPet().getName());

    }

    @Test
    public void deletePet() {
       DeletePetRequest request = new DeletePetRequest();
        request.setId(6);
        DeletePetResponse response = clinicServiceEndpoints.deletePet(request);
        assertNotNull(response);
        assertEquals("if deleting is successful, the response will contain: ", -1, response.getId());

    }

   @Test
   public void deleteVisit() {
       DeleteVisitRequest request = new DeleteVisitRequest();
       request.setId(1);
       DeleteVisitResponse response = clinicServiceEndpoints.deleteVisit(request);
       assertNotNull(response);
       assertEquals("if deleting is successful, the response will contain: ", -1, response.getId());

   }

    @Test
    public void deleteVet() {
        DeleteVetRequest request = new DeleteVetRequest();
        request.setId(1);
        DeleteVetResponse response = clinicServiceEndpoints.deleteVet(request);
        assertNotNull(response);
        assertEquals("if deleting is successful, the response will contain: ", -1, response.getId());

    }

}
