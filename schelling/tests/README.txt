CS 121: Schelling Model of Housing Segregation
Anne Rogers
August 2014, Oct 2014

README.txt: this file

isSatisfied.txt: test description file for Schelling.isSatisfied: Here
    are a few lines from the file

        # gridFileName R threshold i j expectedResult

       test0 tests/grid4.txt 1 0.45  1 1 true
       test1 tests/grid4.txt 1 0.55  1 1 false


    The format for describing a test is: the name of the test, the
    name a grid file, a value for R, a value for the satisfaction
    threshold, the row and column indexes for a location, and either
    the string true or the string false depending on whether the
    homeowner at the specified location is satisfied for the given the
    neighborhood and satisfaction parameters.


doSimulation.txt: test description file for Schelling.doSimulation

  Here are a few lines from the file

    # testname inputGrid R threshold maxSteps expectedGrid
    test0 tests/grid4.txt 1 0.51 3 tests/grid4-after-move-7.txt

  A test description: the name of the test, the name of the input grid
  file, a value for R, a value for the satisfaction threshold, the
  maximum number of steps to complete, and the name of a grid file
  that contains the expected final state of the grid.

Sample Grids: 

  This directory contains a sample set of grids: The file format is
  simple. The first line contains the grid size. Each subsequent line
  contains information for a single row, starting with row 0. A "B"
  means that the corresponding location has a blue homeowner, an "R"
  means that the corresponding location has a red homeowner, and an
  "O" means that the location is open.  See the file
  ``tests/grid4.txt``, which contains the initial grid from the figure
  above, for an example.

  grid0.txt: sample grid
  grid1-1-75-5-expected-result.txt: expected output from a
     call to doSimulation with R=1, threshold=.75, and maxSteps=5
  grid0-1-51-2-expected-result.txt: expected output from a call to doSimulation
    with R=1, threshold=.51, and maxSteps=2

  grid1.txt: sample grid
  tests/grid1-1-75-5-expected-result.txt: expected output from a call to doSimulation
   with R=1, threshold=.75, and maxSteps=5

  grid4.txt, grid4-after-step1.txt, grid4-after-step2.txt,
  grid4-after-step3.txt grid4-after-step4.txt (first pass over grid
    ends in this state), grid4-after-step5.txt, grid4-after-step6.txt
  grid4-after-step7.txt (stabilizes in this state after two steps):
    sample grid and moves from assignment description using R = 1,
    threshold=.51.

  grid5.txt: lone homeowner in city.

  grid-150-30-60.txt: sample grid that with unbalanced city:
  grid-150-30-60-2-40-3-expected-result.txt: expected output from a
    call to doSimulation with R=2, threshold=.40, and maxSteps=3

  grid-150-40-40.txt: sample grid that with balanced city and plenty
    of open homes
  grid-150-40-40-1-33-10-expected-result.txt: expected output from a
    call to doSimulation with R=1, threshold=.33, and maxSteps=10
  grid-150-40-40-2-40-10-expected-result.txt: expected output from a
    call to doSimulation with R=2, threshold=.40, and maxSteps=10

  grid-150-4975-4975.txt: sample grid with a balanced city that is
    more densely populated
  grid-150-4975-4975-2-40-10-expected-result.txt: expected output from a
    call to doSimulation with R=2, threshold=.40, and maxSteps=10


  The ``DrawGrid`` class contains code for drawing a picture of a
  grid. Once you compile it, you can run it from within your pa3
  directory with the name of a grid file as an argument.  For example:

       java DrawGrid tests/grid4.txt

  FYI, the program will not exit until you close the figure.

  The ``DrawGridWitHSat`` class contains code for drawing a picture of
  a grid that includes an indication of whether a homeowner is
  satisfied.  This class will compile only if you have an isSatisfied
  function as described in the assignment description








