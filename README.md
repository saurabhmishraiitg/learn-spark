# learn-spark

Playground for Spark - Python/Scala

- [learn-spark](#learn-spark)
  - [Spark with Jupyter](#spark-with-jupyter)

## Spark with Jupyter

We can work with Spark (PySpark/Spark Scala) using jupyter notebooks. Easiest way to get this up and running is to use a docker image. Found a handy image online with jupyter and spark(python/scala) pre-installed to leverage

> [Official Guide](https://jupyter-docker-stacks.readthedocs.io/en/latest/using/specifics.html#apache-spark)

- Pull docker image
  - `docker pull jupyter/all-spark-notebook`
- Run the image loading external notebook in volumes
  - `docker run -it -p 7777:8888 -p 4040:4040 -v $HOME/github/learn-spark:/home/jovyan jupyter/all-spark-notebook`
    - `rm` Automatically remove the container when it exits
    - `i` Interactive
    - `t` Allocate a pseudo TTY
    - `v` Bind mount a volume
    - `p` Publish a container's port onto host
    - *We are using 7777 port to prevent it from conflicting with an existing jupyter notebook running on local*
    - To prevent password/token prompt from jupyter notebook, we are pointing it to a git repo having password details
