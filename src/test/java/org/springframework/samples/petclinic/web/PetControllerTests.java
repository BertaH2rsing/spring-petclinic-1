package org.springframework.samples.petclinic.web;


import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ContextConfiguration(locations = {"classpath:spring/business-config.xml"})
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("jpa")

public class PetControllerTests {

    private MockMvc mockMvc;

    @Autowired
    private PetController petController;

    @Autowired
    private ClinicService clinicService;

    @Before
    public void setup() {
        this.mockMvc = standaloneSetup(new PetController(clinicService)).build();
    }


    public Pet generatePet() {
        Pet pet = new Pet();
        pet.setName("Pitsuu");
        Owner owner = new Owner();
        owner.setId(1);
        pet.setOwner(owner);
        PetType type = new PetType();
        type.setName("koer");
        type.setId(1);
        pet.setType(type);
        DateTime time = new DateTime();
        pet.setBirthDate(time);
        return pet;
    }

    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @Test
    @Transactional
    public void getPet() throws Exception {
        ObjectMapper mapper = getObjectMapper();
        MvcResult result = mockMvc.perform(get("/pets/get/{petId}", 2)).andExpect(status().isOk()).andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        Pet pet = mapper.readValue(contentAsString, Pet.class);
        assertEquals("Should return pet with name: ", pet.getName(), "Basil");
    }

    //@Test(expected = NestedServletException.class)
    @Test
    @Transactional
    public void getPet_shouldReturn404EntityDoesntExist() throws Exception {
        mockMvc.perform(get("/pets/get/{petId}", 22)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getPetByQuery() throws Exception {
        ObjectMapper mapper = getObjectMapper();
        MvcResult result = mockMvc.perform(get("/pets/get?id=1")).andExpect(status().isOk()).andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        Pet pet = mapper.readValue(contentAsString, Pet.class);
        assertEquals("Should return pet with name: ", pet.getName(), "Leo");
    }

    @Test
    @Transactional
    public void initCreationForm() throws Exception {
        MvcResult result = mockMvc.perform(get("/owners/{ownerId}/pets/new", 1)).andExpect(status().isOk()).andReturn();
        assertEquals("should return modelAndView with viewName: ", "pets/createOrUpdatePetForm", result.getModelAndView().getViewName());
    }


    @Test
    @Transactional
    public void processCreationForm() throws Exception {
        Pet pet = generatePet();
        MvcResult result = mockMvc.perform(post("/owners/{ownerId}/pets/new", 1).sessionAttr("pet", pet)).andExpect(status().is(302)).andReturn();
        assertEquals("should return modelAndView with viewName: ", "redirect:/owners/{ownerId}", result.getModelAndView().getViewName());
    }


    @Test
    @Transactional
    public void initUpdateForm() throws Exception {
        MvcResult result = mockMvc.perform(get("/owners/*/pets/{petId}/edit", 1)).andExpect(status().isOk()).andReturn();
        assertEquals("should return modelAndView with viewName: ", "pets/createOrUpdatePetForm", result.getModelAndView().getViewName());
    }

    @Test
    @Transactional
    public void processUpdateForm() throws Exception {
        Pet pet = generatePet();
        MvcResult result = mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", 1, 1).sessionAttr("pet", pet)).andExpect(status().is(302)).andReturn();
        assertEquals("should return modelAndView with viewName: ", "redirect:/owners/{ownerId}", result.getModelAndView().getViewName());
    }
}
