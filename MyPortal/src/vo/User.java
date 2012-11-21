package vo;

import java.util.List;

public class User
{
	private int id;
	private String name;
	private int age;
	private int gender;
	private Gender genderObj;
	private int experience;
	private Experence experienceObj;
	private List<Integer> interests;
	private List<Interest> interestObjs;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getAge()
	{
		return age;
	}

	public void setAge(int age)
	{
		this.age = age;
	}

	public int getGender()
	{
		return gender;
	}

	public void setGender(int gender)
	{
		this.gender = gender;
	}

	public Gender getGenderObj()
	{
		return genderObj;
	}

	public void setGenderObj(Gender genderObj)
	{
		this.genderObj = genderObj;
	}

	public int getExperience()
	{
		return experience;
	}

	public void setExperience(int experience)
	{
		this.experience = experience;
	}

	public Experence getExperienceObj()
	{
		return experienceObj;
	}

	public void setExperienceObj(Experence experienceObj)
	{
		this.experienceObj = experienceObj;
	}

	public List<Integer> getInterests()
	{
		return interests;
	}

	public void setInterests(List<Integer> interests)
	{
		this.interests = interests;
	}

	public List<Interest> getInterestObjs()
	{
		return interestObjs;
	}

	public void setInterestObjs(List<Interest> interestObjs)
	{
		this.interestObjs = interestObjs;
	}
}
