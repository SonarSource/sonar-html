<template>
  <Table>
    <CustomTableHeader />
    <TableRow />
  </Table>

  <!-- A native table without a literal header remains noncompliant. -->
  <table>
    <CustomTableHeader />
  </table>

  <!-- Headers inside a nested table component do not belong to the native table. -->
  <table>
    <Table>
      <tr><th>Name</th></tr>
    </Table>
  </table>
</template>
