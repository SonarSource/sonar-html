<template>
  <Table>
    <CustomTableHeader />
    <TableRow />
  </Table>

  <!-- A native table without a literal header remains noncompliant. -->
  <table>
    <CustomTableHeader />
  </table>
</template>
