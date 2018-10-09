export class Filter {
  type: string;
}

export class TextFilter extends Filter {
  validString: string;
  targetField: string;
}

export class LookupFilter extends Filter {
  targetField: string;
  targetValues: string[];
}
