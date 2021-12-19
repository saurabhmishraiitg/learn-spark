# learn-spark

Playground for Spark - Python/Scala

- [learn-spark](#learn-spark)
  - [Installation](#installation)
    - [Using Docker](#using-docker)
    - [Using pip](#using-pip)
    - [Using a local pyspark package](#using-a-local-pyspark-package)

## Installation

### Using Docker

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

### Using pip

- [Reference](https://spark.apache.org/docs/latest/api/python/getting_started/install.html)
- Create a new  `conda` environment

  ```bash
  conda create --name pyspark python=3.7
  conda activate pyspark
  conda deactivate
  ```

- Install specific `pyspark` version
  - `pip index versions pyspark`
  - `pip install pyspark==2.4.8`
- Install addons
  - Spark SQL
    - `pip install pyspark[sql]`
  - Pandas API with plotly
    - `pip install pyspark[pandas_on_spark] plotly`
  - To work with Jupyter notebooks
    - `conda install -c conda-forge --name pyspark ipykernel -y`

`PySpark 2.4.8 is compatible with only Python 3.7. Hence when creating a conda environment make sure to use Python 3.7`

### Using a local pyspark package

- Create a new  `conda` environment

  ```bash
  conda create --name pyspark python=3.7
  conda activate pyspark
  conda deactivate
  ```

- Install `findspark` module
  - `conda install -c conda-forge findspark -y`
