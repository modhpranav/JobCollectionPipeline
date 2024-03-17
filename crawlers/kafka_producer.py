import time  # Import the time module

from confluent_kafka import Producer


class KafkaProducer:

    def __init__(self, topic_name):

        config = {"bootstrap.servers": "localhost:9092"}
        self.producer = Producer(config)
        self.topic_name = topic_name


    def delivery_report(self, err, msg):
        """Called once for each message produced to indicate delivery result."""
        if err is not None:
            print("Message delivery failed: {}".format(err))
        else:
            time.sleep(1)  # Add a delay of 1 second
            # print("Message delivered to {} [{}]".format(msg.topic(), msg.partition()))


    def publish_message(self, key, value):
        """Publish a single message to Kafka."""
        self.producer.produce(
            self.topic_name, value=value, key=key, callback=self.delivery_report
        )
        self.producer.poll(0)

        self.producer.flush()

data = {
    "postingURL": "https://www.linkedin.com/jobs/view/2820000000",
    "jobTitle": "Data Engineer",
    "company": "Penn Foster",
    "jobDescription":"Position Summary:Do you have a passion for data? Do you want to be part of a company with a mission to improve people’s lives? If so, Penn Foster has the job for you. Come be a part of Penn Foster Group Data Engineering team. You will play a critical role in this highly visible and strategic team which is revolutionizing Penn foster’s data capabilities. The Data Engineer is primarily responsible for the development of Python/PySpark code in Databricks as well as developing and maintaining Django applications deployed with Docker. This role will have the opportunity to be involved in ML Ops though the development of ML metadata applications.Essential Job Functions:Developing Python, SQL, Django, and PySpark based applications and data flows Participating in code reviews Estimating level of effort for assigned tasks and adhering to schedules Worked in with Agile development processes (Kanban) Be a complete team player Comfortable working in a fluid constantly changing environment Strong sense of ownership for all work Knowledge, Skills, Abilities:3+ years of Data Engineering experience 3+ years of Python and Spark 3+ years of SQL 3+ years developing Django applications Docker and container deployment experience Ability to learn and absorb existing and new data structures Jira and Git exposure Experience working in a cloud environment (Azure preferred) Databricks exposure is a huge plus Tableau experience is a plus ML Ops experience is a plus Familiarity with Agile and Iterative Development (Kanban preferred) Excellent interpersonal and communication skills (written and verbal) Ability to work independently and in a group Self-starter attitude with initiative Creativity Ability to solve complex problems About Us: At Penn Foster Group, we are transforming online learning to help learners by bringing together Penn Foster, CareerStep, Ashworth College, James Madison High School, the New York Institute of Photography, the New York Institute of Art and Design, and other education platforms. Together, we create an accelerated path to greater economic mobility through real-world skills and knowledge that enable learners to achieve long-term success in the workplaces of the future. Our history dates back to 1890 when our founder, Thomas Foster, pioneered distance education by offering training by mail for coal miners to get the necessary skills for safer jobs. Today, with the partners who use our education and training programs, we continue that mission of providing accessible training and education for in-demand skills and are building a workforce that’s prepared for the future job market.Equal Employment Opportunity: We strive toward Diversity, Equity, and Inclusion at Penn Foster Group by intentionally building teams that are diverse – in identities, lived experiences, and ideas to create a culture where people feel connected to each other and have a sense of belonging. We value diversity, equity, and inclusion because it is the foundation that enables us to achieve what we set out to do as an organization – from maximizing the number of learners who can reach their goals while giving them the kinds of experiences we want them to have, to becoming the type of company we want to work in.What We Offer: We offer a robust benefits package that includes medical, dental, vision, flexible spending, generous paid time off, sponsored volunteer opportunities, a 401K with a company match, plus free access to all of our online programs."}
import json
# KafkaProducer("job-details-topic").publish_message("N", json.dumps(data))