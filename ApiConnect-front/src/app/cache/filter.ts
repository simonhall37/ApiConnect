export class Filter {
  type: string;
  editMode: boolean = false;
}

export class TextFilter extends Filter {
  validString: string;
  targetField: string;
}

export class LookupFilter extends Filter {
  targetField: string;
  targetValues: string[];
}
