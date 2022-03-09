package Model;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class AdvertisingAgent {
    private int agentId;
    private String firstName, lastName, phone, agentCode, email;

    public AdvertisingAgent(int agentId, String firstName, String lastName, String phone, String agentCode, String email) {
        this.agentId = agentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.agentCode = agentCode;
        this.email = email;
    }

    public int getAgentId() {
        return agentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAgentCode() {
        return agentCode;
    }

    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
